package com.scc;

import com.scc.dao.BookDao;
import com.scc.ui.LibraryMenu;

public class Main {

    public static void main(String[] args) {
        BookDao dao = new BookDao();
        new LibraryMenu(dao).start();
    }
}