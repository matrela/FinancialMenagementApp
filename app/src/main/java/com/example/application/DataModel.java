package com.example.application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class DataModel {

    private int id;
    private String name;
    private float amount;
    private String category;
    private String date;
    private byte[] image;

    public DataModel(int id, String name, float amount, String category, String date, byte[] image) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.image = image;
    }

    // toString for printing the content of class objects
    @Override
    public String toString() {
        return "DataModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", image='" + Arrays.toString(image) + '\'' +
                '}';
    }

    public static Comparator<DataModel> NameAZComparator = new Comparator<DataModel>() {
        @Override
        public int compare(DataModel d1, DataModel d2) {
            return d1.getName().compareTo(d2.getName());
        }
    };

    public static Comparator<DataModel> NameZAComparator = new Comparator<DataModel>() {
        @Override
        public int compare(DataModel d1, DataModel d2) {
            return d2.getName().compareTo(d1.getName());
        }
    };

    public static Comparator<DataModel> DateAscendingComparator = new Comparator<DataModel>() {
        @Override
        public int compare(DataModel d1, DataModel d2) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date1;
            try {
                date1 = sdf.parse(d1.getDate());
                Date date2 = sdf.parse(d2.getDate());
                if (date1 != null) {
                    if(date1.compareTo(date2) > 0) {
                        // Date 1 occurs after Date 2
                        return 1;
                    } else if(date1.compareTo(date2) < 0) {
                        // Date 1 occurs before Date 2
                        return -1;
                    } else if(date1.compareTo(date2) == 0) {
                        // Both dates are equal
                        return 0;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    public static Comparator<DataModel> DateDescendingComparator = new Comparator<DataModel>() {
        @Override
        public int compare(DataModel d1, DataModel d2) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date2;
            try {
                date2 = sdf.parse(d1.getDate());
                Date date1 = sdf.parse(d2.getDate());
                if (date2 != null) {
                    if(date2.compareTo(date1) < 0) {
                        // Date 2 occurs after Date 1
                        return 1;
                    } else if(date2.compareTo(date1) > 0) {
                        // Date 2 occurs before Date 1
                        return -1;
                    } else if(date2.compareTo(date1) == 0) {
                        // Both dates are equal
                        return 0;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    public static Comparator<DataModel> AmountAscendingComparator = new Comparator<DataModel>() {
        @Override
        public int compare(DataModel d1, DataModel d2) {
            float change1 = d1.getAmount();
            float change2 = d2.getAmount();
            if (change1 < change2) return -1;
            if (change1 > change2) return 1;
            return 0;
        }
    };

    public static Comparator<DataModel> AmountDescendingComparator = new Comparator<DataModel>() {
        @Override
        public int compare(DataModel d1, DataModel d2) {
            float change1 = d1.getAmount();
            float change2 = d2.getAmount();
            if (change2 < change1) return -1;
            if (change2 > change1) return 1;
            return 0;
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
