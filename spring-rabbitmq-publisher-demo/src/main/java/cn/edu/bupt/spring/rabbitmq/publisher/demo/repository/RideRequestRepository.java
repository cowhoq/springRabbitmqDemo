package cn.edu.bupt.spring.rabbitmq.publisher.demo.repository;

import cn.edu.bupt.spring.rabbitmq.publisher.demo.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    Optional<RideRequest> findByMessageId(String messageId);
}
