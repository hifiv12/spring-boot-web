package org.zerock.ex2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.ex2.entity.Memo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    // save, findById, findALl, deleteById 등 기본 쿼리를 수행하는 메서드는 구현되어 있음
    // where 절에 조건이 많거나 join 구문은 쿼리 메서드나 jpql 혹은 native query 사용

    // 쿼리 메서드
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);

    Page<Memo> findByMnoBetween(Long from, Long to , Pageable pageable);

    void deleteMemoByMnoLessThan(Long num);

    // Jpql
    @Query("select m from m Memo m order by m.mno desc")
    List<Memo> getListDesc();

    @Transactional
    @Modifying
    @Query("update Memo m set m.memoText = :memoText where m.mno = :mno")
    int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText);

    @Transactional
    @Modifying
    @Query("update Memo m set m.memoText = :#{#param.memoText} where m.mno = :#{#param.mno}")
    int updateMemoText(@Param("param") Memo memo);

    // Jpql로 페이징 처리시 카운트쿼리 필요
    @Query(value = "select m from Memo m where m.mno > :mno",
        countQuery = "select count(m) from Memo m where m.mno > :mno")
    Page<Memo> getListWithQuery(Long mno, Pageable pageable);

    //Jpql의 함수 결과값을 처리할 때 Object[]를 사용
    @Query(value = "select m.mno, m.memoText, CURRENT_DATE from Memo m where m.mno > :mno",
        countQuery = "select count(m) from Memo m where m.mno < :mno")
    Page<Object[]> getListWithQueryObject();

    // native query
    @Query(value = "select * from tbl_memo where mno > 0",
        nativeQuery = true)
    List<Object[]> getNativeResult();
}
