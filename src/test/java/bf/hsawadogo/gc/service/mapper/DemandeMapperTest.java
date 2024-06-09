package bf.hsawadogo.gc.service.mapper;

import static bf.hsawadogo.gc.domain.DemandeAsserts.*;
import static bf.hsawadogo.gc.domain.DemandeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DemandeMapperTest {

    private DemandeMapper demandeMapper;

    @BeforeEach
    void setUp() {
        demandeMapper = new DemandeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDemandeSample1();
        var actual = demandeMapper.toEntity(demandeMapper.toDto(expected));
        assertDemandeAllPropertiesEquals(expected, actual);
    }
}
