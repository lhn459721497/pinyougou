import com.itheima.demo.QueueProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-jms-producer.xml")
public class TestQueue {

    @Autowired
    private QueueProducer queueProducer;

    @Test
    public void testSend(){

        queueProducer.sendTextMessage("springJms-点对点");

    }

}
