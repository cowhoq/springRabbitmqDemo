package cn.edu.bupt.spring.rabbitmq.consumer.demo.Listener;

import cn.edu.bupt.spring.rabbitmq.consumer.demo.model.RideRequest;
import cn.edu.bupt.spring.rabbitmq.consumer.demo.repository.RideRequestRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;

@Component
@RabbitListener(queues = "rideRequestQueue") // 监听的队列名称 rideRequestQueue
public class DirectReceiver {

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @RabbitHandler
    public void process(Map<String, Object> rideRequestMessage) {
        try {
            // 延时 5 秒
            for (int i = 0; i < 5; i++) {
                System.out.println("延时1s");
                Thread.sleep(1000);
            }


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("延时被中断: " + e.getMessage());
            return;
        }
        String messageId = (String) rideRequestMessage.get("messageId");

        Optional<RideRequest> optionalRideRequest = rideRequestRepository.findByMessageId(messageId);

        if (optionalRideRequest.isPresent()) {
            RideRequest rideRequest = optionalRideRequest.get();
            rideRequest.setAccepted(true); // 更新打车请求状态为已接单
            rideRequestRepository.save(rideRequest); // 保存更新后的状态

            System.out.println("DirectReceiver消费者收到消息并更新状态: " + rideRequestMessage.toString());
        } else {
            System.out.println("DirectReceiver消费者收到消息，但未找到对应的打车请求: " + rideRequestMessage.toString());
        }
    }
}
