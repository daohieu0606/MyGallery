package com.example.mygallery.data.sorthelper;

import com.example.mygallery.data.model.MyPicture;

import java.util.Comparator;

public class PhotoComparator {
    public static Comparator<MyPicture> getComparator(SortingMode sortingMode) {
        Comparator<MyPicture> result = null;

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

    private static Comparator<MyPicture> getTypeComparator() {
        return new TypeComparator();
    }

    private static Comparator<MyPicture> getSizeComparator() {
        return new SizeComparator();
    }

    private static Comparator<MyPicture> getNameComparator() {
        return new NameComparator();
    }

    private static Comparator<MyPicture> getDateComparator() {
        return new DateComparator();
    }

    static class NameComparator implements Comparator<MyPicture> {

        @Override
        public int compare(MyPicture o1, MyPicture o2) {
            return o1.getpContent().getPicturName().compareTo(o2.getpContent().getPicturName());
        }
    }
    static class TypeComparator implements Comparator<MyPicture> {

        @Override
        public int compare(MyPicture o1, MyPicture o2) {
            String type1 = o1.getpContent().getPicturePath().substring(o1.getpContent().getPicturePath().lastIndexOf('.'));
            String type2 = o2.getpContent().getPicturePath().substring(o2.getpContent().getPicturePath().lastIndexOf('.'));
            return type1.compareTo(type2);
        }
    }
    static class SizeComparator implements Comparator<MyPicture> {

        @Override
        public int compare(MyPicture o1, MyPicture o2) {
            return Integer.compare((int) o1.getpContent().getPictureSize(),(int) o2.getpContent().getPictureSize());
        }
    }
    static class DateComparator implements Comparator<MyPicture> {
        @Override
        public int compare(MyPicture o1, MyPicture o2) {
            return Integer.compare((int) o1.getpContent().getDate_added(),(int) o2.getpContent().getDate_added());
        }
    }
}
