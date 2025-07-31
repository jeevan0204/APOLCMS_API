
package in.apcfss.entities;

import org.springframework.data.jpa.domain.AbstractPersistable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "my_entity")
public class MyEntity extends AbstractPersistable<Long> {
	String myFiled;
}
   