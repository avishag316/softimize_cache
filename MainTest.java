import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.spi.PersistenceProvider;
import java.util.*;

//Class for Testing
public class MainTest {
	public static void main(String[] args){
		//Create/Open and update Person DB
		PersistenceProvider pr = new ProviderDB();
		EntityManagerFactory emf =
	            pr.createEntityManagerFactory("fileEntity.odb",null);
	    EntityManager em = emf.createEntityManager();
	    //Update the DB
	    UpdateDB(em);
        
	    //Create Person cache
    	Cache<Person> cache = new Cache<Person>(Person.class, pr, "fileEntity.odb", null);
	    cache.PrintAll();
		
		Person entity2 = new Person(23456781,"Yosi");
		Person entity3 = new Person(34567812,"Dani");
		
		Subscriber sub5 = new Subscriber(56781234);
		Subscriber sub6 = new Subscriber(67812345);
		
		//Add a subscriber as a 'notification listener'
		cache.addNotificationListener(sub5, null, null);
		cache.addNotificationListener(sub6, null, null);
		
		//Add
		cache.AddEntity(entity2);
		cache.AddEntity(entity3);
		
		System.out.println("After add 1/2:");
		cache.PrintAll();
			
		//Get
		System.out.println("Get: " + cache.GetEntity(entity2.getId()));
		
		//Update
		entity2 = new Person(23456781, "Avi");
		cache.UpdateEntity(entity2);
		System.out.println("After update:(Avi)");
		cache.PrintAll();
		
		//Remove
		cache.RemoveEntity(entity2.getId());
		System.out.println("After remove:(yosi)");
		cache.PrintAll();
		
		cache.Close();
	}
	
	public static void UpdateDB(EntityManager em){
		//Add 2 Person to the DB
		boolean addP1 = true;
	    boolean addP2 = true;
	    em.getTransaction().begin();
        Person p1 = new Person(45678123,"Avishag");
        Person p2 = new Person(56781234,"David");
	
	    CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<Person> q = cb.createQuery(Person.class);
	    Root<Person> c = q.from(Person.class);
	    q.multiselect(c); 
	    TypedQuery<Person> query = em.createQuery(q);
	    List<Person> results = query.getResultList();
	    for (Person p : results)
	    {
	    	if (p.getId() == p1.getId())
	    	{
	    		addP1 = false;
	    	}
	    	if (p.getId() == p2.getId()){
	    		addP2 = false;
	    	}
	    }
	    if(true == addP1){
	    	em.persist(p1);
	    }
	    if(true == addP2){
	    	em.persist(p2);
	    }
	    em.getTransaction().commit();
	}
}
