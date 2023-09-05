package org.zerock.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;
import org.zerock.guestbook.repository.GuestbookRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService{

    private final GuestbookRepository repository;

    @Override
    public Long register(GuestbookDTO dto) {
        log.info("DTO-------------");
        log.info(dto);

        Guestbook entity = dtoToEntity(dto);
        log.info(entity);

        repository.save(entity);

        return entity.getGno();
    }

    // 검색 조건 없는 리스트 페이지
//    @Override
//    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
//        // 현재 넘어온 값은 requestDTO.page=11, size=0 나머지는 모름
//        // 페이지 처리를 위해 pageable에 page, size, sort값을 넣어준다.
//        Pageable pageable = requestDTO.getPageable(Sort.by("gno").ascending());
//
//        // result에는 GuestbookRepository의 JPA를 이용해 pageable에 따른 처리의 반환값을 저장
//        Page<Guestbook> result = repository.findAll(pageable);
//
//        // 람다식으로 엔티티가 명시된 함수의 반환형으로 GuestService의 함수 entity에서 dto로 바꿔주는 함수를 넣는다.
//        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));
//
//        // 그럼 인터페이스의 명시대로
//        return new PageResultDTO<>(result, fn);
//    }

    // 검색 조건 있는 리스트 페이지
    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        BooleanBuilder booleanBuilder = getSerch(requestDTO);

        Page<Guestbook> result = repository.findAll(booleanBuilder, pageable);

        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<Guestbook> result = repository.findById(gno);

        Guestbook gb = result.get();

        return result.isPresent()? entityToDto(result.get()): null;
    }

    @Override
    public void modify(GuestbookDTO dto) {

        // 업데이트 항목 : 제목, 내용
        Optional<Guestbook> result = repository.findById(dto.getGno());

        if(result.isPresent()) {

            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }

    @Override
    public void remove(Long gno) {

        repository.deleteById(gno);
    }

    private BooleanBuilder getSerch(PageRequestDTO requestDTO) {

        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qGuestbook.gno.gt(0L);

        booleanBuilder.and(expression);

        if(type == null || type.trim().length() == 0) { // 검색 조건이 없는 경우
            return booleanBuilder;
        }

        // 검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("t")) {
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        }
        if(type.contains("c")) {
            conditionBuilder.or(qGuestbook.content.contains(keyword));
        }
        if(type.contains("W")) {
            conditionBuilder.or(qGuestbook.writer.contains(keyword));
        }

        // 모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }

}
