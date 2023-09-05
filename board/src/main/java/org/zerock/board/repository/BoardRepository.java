package org.zerock.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.board.entity.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {


    // 클래스 내부에 참조가 있는 경우
    @Query("select b, w from Board b left join b.writer w where b.bnp =:bno")
    Object getBoardWithWriter(@Param("bno") Long bno);

    // 연관관계가 없는 경우 명시적으로 on을 이용해서 작성
    @Query("SELECT b, r FROM Board b LEFT JOIN Reply r ON r.board = b WHERE b.bno = :bno")
    List<Object[]> getBoardWithReply(@Param("bno") Long bno);

    @Query(value ="SELECT b, w, count(r) " +
            " FROM Board b " +
            " LEFT JOIN b.writer w " +
            " LEFT JOIN Reply r ON r.board = b " +
            " GROUP BY b",
            countQuery ="SELECT count(b) FROM Board b")
    Page<Object[]> getBoardWithReplyCount(Pageable pageable);
}
