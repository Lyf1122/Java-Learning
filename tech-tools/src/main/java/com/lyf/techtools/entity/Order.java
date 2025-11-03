package com.lyf.techtools.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Order entity represents an order in the system.
 * It contains information about the user who made the order,
 * the total amount of the order, and the creation time of the order.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Double total;

  @Column(nullable = false)
  private LocalDateTime createAt;

  /**
   * This method is called before the entity is persisted to the database.
   * It sets the creation time of the order to the current time
   * if it has not been set already.
   */
  @PrePersist
  public void prePersist() {
    if (createAt == null) {
      createAt = LocalDateTime.now();
    }
  }
}
