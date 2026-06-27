package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //все брони пользователя
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    //текущие брони пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBooker(@Param("userId") Long userId,
                                      @Param("now")LocalDateTime now);

    //будущие брони пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByBooker(@Param("userId") Long userId,
                                     @Param("now") LocalDateTime now);

    //прошлые брони пользователя
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByBooker(@Param("userId") Long userId,
                                   @Param("now") LocalDateTime now);

    //статус брони пользователя
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    //все брони вещей владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwner(@Param("ownerId") Long ownerId);

    //текущие брони вещей владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("now") LocalDateTime now);

    //будущие брони вещей владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    //прошлые брони вещей владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId,
                                  @Param("now") LocalDateTime now);

    //ожидающие брони вещей владельца
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId,
                                       @Param("status") BookingStatus status);

    //брал ли пользователь вещь в аренду
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now")
    boolean existsApprovedBookingByUser(@Param("itemId") Long itemId,
                                        @Param("userId") Long userId,
                                        @Param("now") LocalDateTime now);

    //поиск последнего бронирования для вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findLastApprovedBookingsByItem(@Param("itemId") Long itemId);

    //поиск следующего бронирования для вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start > :now " +
            "ORDER BY b.start ASC")
    List<Booking> findNextApprovedBookingsByItem(@Param("itemId") Long itemId,
                                                 @Param("now") LocalDateTime now);

}
