package cn.newphy.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.newphy.data.id.IdMaker;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class IdMakerTest {

	@Test
	public void testMakeObjectId() {
		for(int i = 0; i< 100; i++) {
			String objectId = IdMaker.makeObjectId();
			System.out.println(objectId);
		}
	}
	
	
	@Test
	public void testMakeNextId() {
		try {
			while(true) {
				System.out.println(IdMaker.nextId());
				for(int i = 0; i< 5; i++) {
					long id = IdMaker.nextId("T" + i);
					System.out.println(id);
				}
				System.out.println("--------sleep 5s ---------------");
				Thread.sleep(5000L);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
