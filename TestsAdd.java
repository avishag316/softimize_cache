import static org.junit.Assert.*;

import org.junit.Test;

public class TestsAdd{
	@Test
	public void TestCacheAdd(){
		Cache<Person> cache = new Cache<Person>();
		Person entity2 = new Person(23456781,"Yosi");
		Person entity3 = new Person(34567812,"Dani");
		
		cache.AddEntity(entity2);
		cache.AddEntity(entity3);
		
		try{
			assertEquals("add fault",entity2,cache.GetEntity(entity2.getId()));
			System.out.println("add ok 1/2");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
		
		try{
			assertEquals("add fault",entity3,cache.GetEntity(entity3.getId()));
			System.out.println("add ok 2/2");
		}
		catch (AssertionError e){
			System.out.println(e.getMessage());
		}
	}
}
