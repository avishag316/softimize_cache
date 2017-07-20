import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

//Class for Testing
public class ProviderDB implements PersistenceProvider{
	public ProviderUtil getProviderUtil() {
		return null;
	}
	
	public boolean generateSchema(String arg0, Map arg1) {
		return false;
	}
	
	public void generateSchema(PersistenceUnitInfo arg0, Map arg1) {}
	
	public EntityManagerFactory createEntityManagerFactory(String arg0, Map arg1) {
		return Persistence.createEntityManagerFactory(arg0, arg1);
	}
	
	public EntityManagerFactory createContainerEntityManagerFactory(
			PersistenceUnitInfo arg0, Map arg1) {
		return null;
	}
}
