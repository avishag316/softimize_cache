import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.spi.PersistenceProvider;

public class Cache<T> extends NotificationBroadcasterSupport{
	//The cache of entities 
	private HashMap<Integer, T> entities = null;
	
	//SequenceNumber of notifications
	private int sequenceNumberAdd;
	private int sequenceNumberRemove;
	private int sequenceNumberUpdate;
	
	private Object lockEntitiesMap = null;
	private Object lockSequenceNumberAdd = null;
	private Object lockSequenceNumberRemove = null;
	private Object lockSequenceNumberUpdate = null;
	
	//The DB provider
	private PersistenceProvider provider = null;
	private EntityManagerFactory entityManagerFactory= null;
	private EntityManager entityManager= null;
	private Class<T> classType = null;
	
	/**
	* Constructor, without provider
	* @param  Nothing
	* @return Nothing
	*/
	public Cache(){
		//Initialization
		this.entities = new HashMap<Integer,T>();
		
		this.sequenceNumberAdd = 0;
		this.sequenceNumberRemove = 0;
		this.sequenceNumberUpdate = 0;
		
		this.lockEntitiesMap = new Object();
		this.lockSequenceNumberAdd = new Object();
		this.lockSequenceNumberRemove = new Object();
		this.lockSequenceNumberUpdate = new Object();
	}

	/**
	* Constructor with provider
	* @param 'provider' - the provider of DB
	* @param 'nameFile' - the DB's file
	* @param 'map' - for the DB
	* @param 'classDB' - the class implements Entity for the cache
	* @return Nothing 
	*/
	public Cache(Class<T> classDB,PersistenceProvider provider,String nameFile,Map<?, ?> map){
		//Initialization
		this.entities = new HashMap<Integer,T>();
		
		this.sequenceNumberAdd = 0;
		this.sequenceNumberRemove = 0;
		this.sequenceNumberUpdate = 0;
		
		this.lockEntitiesMap = new Object();
		this.lockSequenceNumberAdd = new Object();
		this.lockSequenceNumberRemove = new Object();
		this.lockSequenceNumberUpdate = new Object();
		
		this.provider = provider;
		this.entityManagerFactory= provider.createEntityManagerFactory(nameFile, map);
	    this.entityManager= entityManagerFactory.createEntityManager();
		this.classType = classDB;
		InitCache();
	}
	
	/**
	* Update Db's Entities in the cache
	* @param  Nothing
	* @return Nothing
	*/
	private void InitCache(){
		//Query DB
	    CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
	    CriteriaQuery<T> q = (CriteriaQuery<T>) cb.createQuery(this.classType);
	    Root<T> c = (Root<T>) q.from(this.classType);
	    q.multiselect(c); 
	    TypedQuery<T> query = this.entityManager.createQuery(q);
	    List<T> results = query.getResultList();
	    
	    //Update the cache
	    for (T element : results){
	    	if(!this.entities.containsKey(IdEntityT(element))){
	    		PutHash(element);
	    	}
	    }
	}
	
	/**
	* Put Entity in the cache
	* @param  'element' - to put in the cache
	* @return Nothing
	*/
	private void PutHash(T element){
		//'Convert' T to Entity and put in the cache
		Integer id = IdEntityT(element);
		this.entities.put(id, element);
	}
	
	/**
	* Return element's id
	* @param  'element' - to return element's id
	* @return 'id' - the element's id
	*/
	private Integer IdEntityT(T element){
		Method getIdMethod;
    	Integer id;
    	try {
			getIdMethod = element.getClass().getMethod("getId");
			id = (Integer)getIdMethod.invoke(element);
			return id;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
    	return 0;
	}
	
	/**
	* Add 'entityOne' to the cache
	* @param  'entityOne' this is the parameter to add (to cache)
	* @return Nothing
	*/
	public void AddEntity(T entity){
		Notification notificationAdd;
		
		//Add to the cache
		synchronized (this.lockEntitiesMap) {
			if(null == this.entities){
				this.entities= new HashMap<Integer,T>();
			}
			if(this.entities.containsKey(IdEntityT(entity))){
				return;
			}
			PutHash(entity);
		}
		
		synchronized (this.lockSequenceNumberAdd){
			//Publishes a notification to subscriber objects upon any Add
			notificationAdd = new Notification("add", this, ++this.sequenceNumberAdd);
		    sendNotification(notificationAdd);
		}
		
		//Add to the DB
		if(null != this.provider){
			this.entityManager.getTransaction().begin();
			this.entityManager.persist(entity);
			this.entityManager.getTransaction().commit();
		}
	}
	
	/**
	* Get
	* @param  'idEntity' this is the Entity's id they wanted to get 
	* @return 'EntityOne' this is the Entity they wanted to get
	*/
	public T GetEntity(int idEntity){
		if(null == this.entities || 0 == this.entities.size()){
			return null;
		}
		return this.entities.get(idEntity);
	}

	/**
	* Update Entity object
	* @param  'entityOne' - this is the Entity they wanted to update
	* @return Nothing
	*/
	public void UpdateEntity(T entityOne){
		Notification notificationUpdate;
	    
		//Update the cache
		synchronized (this.lockEntitiesMap) {
			if(null == this.entities|| 0 == this.entities.size()){
				return;
			}
			
			if(!this.entities.containsKey(IdEntityT(entityOne))){
				return;
			}
			PutHash(entityOne);
		}
		
		synchronized (this.lockSequenceNumberUpdate){
			//Publishes a notification to subscriber objects upon any Update
			notificationUpdate = new Notification("update", this, ++this.sequenceNumberUpdate);
		    sendNotification(notificationUpdate);
		}
		
		//Update the DB
		if(null != this.provider){
			//Remove from the DB
			T entity = this.entityManager.find(this.classType,entityOne);
			this.entityManager.getTransaction().begin();
			this.entityManager.remove(entity);
			this.entityManager.getTransaction().commit();
		
			//Add to the DB
			this.entityManager.getTransaction().begin();
			this.entityManager.persist(entityOne);
			this.entityManager.getTransaction().commit();
		}
	}
	
	/**
	* Removes Entity object
	* @param  'idEntity' - this is the Entity's id they wanted to remove
	* @return Nothing
	*/
	public void RemoveEntity(int idEntity){
		Notification notificationRemove;
		
		//Remove from the cache
		synchronized (this.lockEntitiesMap) {
			if(null == this.entities|| 0 == this.entities.size()){
				return;
			}
			
			if(!this.entities.containsKey(idEntity)){
				return;
			}
			this.entities.remove(idEntity);
		}
		
		synchronized (this.lockSequenceNumberRemove) {
			//Publishes a notification to subscriber objects upon any Remove
			notificationRemove = new Notification("remove", this, ++this.sequenceNumberRemove);
			sendNotification(notificationRemove);
		}
		
		//Remove from the DB
		if(null != this.provider){
			T entity = this.entityManager.find(this.classType,idEntity);
			this.entityManager.getTransaction().begin();
			this.entityManager.remove(entity);
			this.entityManager.getTransaction().commit();
		}
	}
	
	/**
	* Prints the cache and the DB
	* @param  Nothing
	* @return Nothing
	*/
	public void PrintAll(){
		//Print the DB
		System.out.println("The DB:");
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
	    CriteriaQuery<T> q = (CriteriaQuery<T>) cb.createQuery(this.classType);
	    Root<T> c = (Root<T>) q.from(this.classType);
	    q.multiselect(c); 
	    TypedQuery<T> query = this.entityManager.createQuery(q);
	    List<T> results = query.getResultList();
	    for (T element : results)
	    {
	    	System.out.println(element);
	    }
		
	    //Print the cache
	    if(null != this.provider){
	    	System.out.println("The cache:");
	    	if(null == this.entities){
	    		return;
	    	}
	    	for (int key: this.entities.keySet()){
	    		System.out.println(this.entities.get(key).toString());
	    	}
	    }
	}
	
	/**
	* Close the cache and the DB
	* @param  Nothing
	* @return Nothing
	*/
	public void Close(){
		this.entities = null;
		// Close the database connection:
        this.entityManager.close();
        this.entityManagerFactory.close();
	}
}
