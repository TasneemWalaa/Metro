module org.example.metro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql; // ضيفيه لو بتستخدمي قاعدة بيانات، لو لسه مش متأكدة سيبيه

    // لازم المسار هنا يطابق الـ package اللي في الكود بالظبط
    opens org.example.metro.controller to javafx.fxml;
    opens org.example.metro.model to javafx.base;

    // لو عندك ملفات fxml في فولدر view، لازم تفتحي المسار ده كمان
    opens org.example.metro to javafx.fxml;

    exports org.example.metro;
    exports org.example.metro.model;
    exports org.example.metro.network;
    exports org.example.metro.controller;
    exports org.example.metro.util;
}