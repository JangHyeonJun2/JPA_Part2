package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;


    @Test
    public void 상품주문() {
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", "김연한", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(1).isEqualTo(getOrder.getOrderItems().size()); //주문한 상품 종류 수가 정확해야한다.
        assertThat(10000*orderCount).isEqualTo(getOrder.getTotalPrice());
        assertThat(8).isEqualTo(book.getStockQuantity()); //주문한 수량만큼 재고가 줄어야한다.

    }


    @Test
    public void 주문취소() {
        //given
        Member member = createMember();
        Book book = createBook("JPA", "kim", 1000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCLE); //주문 취소시 상태는 cancel이다.
        assertThat(10).isEqualTo(book.getStockQuantity()); //주문이 취소된 상품은 그만큼 재고가 증가해야 한다.

    }

//    IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> memberService.join(member1));
//    Assertions.assertThat(illegalStateException.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    @Test()
    public void 상품주문_재고수량초과() {
        //given
        Member member = createMember();
        Item item = createBook("오브젝트","미상",10000,10);

        int orderCount = 12;

        //when
        org.junit.jupiter.api.Assertions.assertThrows(NotEnoughStockException.class, () ->orderService.order(member.getId(),item.getId(),orderCount));
        //then
//        fail("재고 수량 부족 예외가 발생해야합니다.");
    }







    private Book createBook(String name, String author, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","월스트리스","123"));
        em.persist(member);
        return member;
    }
}