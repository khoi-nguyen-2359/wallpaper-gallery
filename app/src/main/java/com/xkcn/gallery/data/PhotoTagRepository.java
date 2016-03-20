package com.xkcn.gallery.data;

import java.util.HashSet;

/**
 * Created by khoinguyen on 2/2/16.
 */
public interface PhotoTagRepository {
    int addTags(HashSet<String> tags);
}
