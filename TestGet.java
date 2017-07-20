import static org.junit.Assert.*;
import org.junit.Test;

public class TestGet{
	@Test
	public void TestCacheGet(){
		Cache<Person> cache = new Cache<Person>();
		Person entity2 = new Person(23456781,"Yosi");
		Person entity3 = new Person(34567812,"Dani");
		
		cache.AddEntity(entity2);
		
		try{
			assertEquals("get fault",entity2, cache.GetEntity(entity2.getId()));
			System.out.println("get ok 1/2");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
		
		try{
			assertNull("get fault",cache.GetEntity(entity3.getId()));
			System.out.println("get ok 2/2");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
	}
}
