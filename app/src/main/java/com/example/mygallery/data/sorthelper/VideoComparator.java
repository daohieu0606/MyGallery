package com.example.mygallery.data.sorthelper;

import com.example.mygallery.data.model.MyVideo;
import java.util.Comparator;

public class VideoComparator {
    public static Comparator<MyVideo> getComparator(SortingMode sortingMode) {
        Comparator<MyVideo> result = null;

        switch (sortingMode) {
            case DATE:
                result = getDateComparator();
                break;
            case NAME:
                result = getNameComparator();
                break;
            case SIZE:
                result = getSizeComparator();
                break;
            case TYPE:
                result = getTypeComparator();
                break;
        }

        return  result;
    }

    private static Comparator<MyVideo> getTypeComparator() {
        return new VideoComparator.TypeComparator();
    }

    private static Comparator<MyVideo> getSizeComparator() {
        return new VideoComparator.SizeComparator();
    }

    private static Comparator<MyVideo> getNameComparator() {
        return new VideoComparator.NameComparator();
    }

    private static Comparator<MyVideo> getDateComparator() {
        return new VideoComparator.DateComparator();
    }

    static class NameComparator implements Comparator<MyVideo> {

        @Override
        public int compare(MyVideo o1, MyVideo o2) {
            return o1.getvContent().getVideoName().compareTo(o2.getvContent().getVideoName());
        }
    }
    static class TypeComparator implements Comparator<MyVideo> {

        @Override
        public int compare(MyVideo o1, MyVideo o2) {
            String type1 = o1.getvContent().getPath().substring(o1.getvContent().getPath().lastIndexOf('.'));
            String type2 = o2.getvContent().getPath().substring(o2.getvContent().getPath().lastIndexOf('.'));
            return type1.compareTo(type2);
        }
    }
    static class SizeComparator implements Comparator<MyVideo> {

        @Override
        public int compare(MyVideo o1, MyVideo o2) {
            return Integer.compare((int) o1.getvContent().getVideoSize(),(int) o2.getvContent().getVideoSize());
        }
    }
    static class DateComparator implements Comparator<MyVideo> {
        @Override
        public int compare(MyVideo o1, MyVideo o2) {
            return Integer.compare((int) o1.getvContent().getDate_added(),(int) o2.getvContent().getDate_added());
        }
    }
}
