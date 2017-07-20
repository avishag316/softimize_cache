import static org.junit.Assert.*;
import org.junit.Test;

public class TestsRemove{
	@Test
	public void TestCacheRemove(){
		Cache<Person> cache = new Cache<Person>();
		Person entity2 = new Person(23456781,"Yosi");
		
		cache.AddEntity(entity2);
		
		cache.RemoveEntity(entity2.getId());
		try{
			assertNull("remove fault",cache.GetEntity(entity2.getId()));
			System.out.println("remove ok 1/1");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
	}
}