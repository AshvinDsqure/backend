package org.dspace.app.rest.utils;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static String getNameWithoutExtension(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? file : file.substring(0, dotIndex);
    }
}
