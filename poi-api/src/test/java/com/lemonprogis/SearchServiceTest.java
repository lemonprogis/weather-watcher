package com.lemonprogis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    public SearchService searchService;

    @Test
    public void shapefileTest() throws Exception {
        searchService.loadStates();
    }
}
