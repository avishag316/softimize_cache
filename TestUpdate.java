import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestUpdate {
	@Test
	public void TestCacheUpdate(){
		Cache<Person> cache = new Cache<Person>();
		Person entity2 = new Person(23456781,"Yosi");
		Person temp = entity2;
		
		cache.AddEntity(entity2);
		
		entity2 = new Person(23456781, "Avi");
		cache.UpdateEntity(entity2);
		
		try{
			assertEquals("update fault",entity2, cache.GetEntity(entity2.getId()));
			System.out.println("update ok 1/2");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
		
		try{
			assertNotEquals("update fault",temp, cache.GetEntity(entity2.getId()));
			System.out.println("update ok 2/2");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
	}
}
