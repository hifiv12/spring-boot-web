package org.zerock.board.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "writer") // ToString은 항상 exclude
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;

    private String content;

    // 기본적으로 EAGER > LAZY로 변경
    @ManyToOne (fetch = FetchType.LAZY) // 지연 로딩
    private Member writer;
}
