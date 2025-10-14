package com.example.demo.Domain;

import java.util.Date;


    public class Student
    {
        private long id;
        private String name;
        private String dept;
        private Date dob;

        public Student(long id, String name, String dept, Date dob) {
            this.id = id;
            this.name = name;
            this.dept = dept;
            this.dob = dob;
        }

        public String getDept() {
            return dept;
        }

        public void setDept(String dept) {
            this.dept = dept;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }



