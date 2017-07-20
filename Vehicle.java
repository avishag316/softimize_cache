import java.lang.annotation.Annotation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//Class for Testing 

@Entity
public class Vehicle implements Entity{
	@Id @GeneratedValue
	private int idEntity;
	private String nameEntity;
	
	public Vehicle(int idEntity,String nameEntity) {
		this.idEntity = idEntity;
		this.nameEntity = nameEntity;
	}
	
	public int getId() {
		return this.idEntity;
	}

	public String getNameEntity(){
		return this.nameEntity;
	}
	
	@Override
	public String toString(){
		String str = Integer.toString(this.idEntity)+ " " + this.nameEntity;
		return str;
	}

	public Class<? extends Annotation> annotationType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String name() {
		// TODO Auto-generated method stub
		return null;
	}
}
