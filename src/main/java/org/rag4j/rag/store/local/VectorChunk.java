package org.rag4j.rag.store.local;

import lombok.*;
import org.rag4j.rag.model.Chunk;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VectorChunk {
    private Chunk chunk;
    private List<Float> vector;

}
