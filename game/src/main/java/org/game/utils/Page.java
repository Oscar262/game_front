package org.game.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {
    private int total;
    private int offset;
    private int limit;
    private int actual_page;
    private int total_page;
    private boolean first;
    private boolean last;
    private List<T> data;
}
