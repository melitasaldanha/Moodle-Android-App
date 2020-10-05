Moodle Android App
~~~~~~~~~~~~~~~~~~~~
Android Studio version used: Android Studio 3.1.1
JDK version: Java SE(10)
Gradle Version: 4.4
Minimum SDK Version: 21
Target SDK Version: 26

This directory contains the full implementation of a basic application for the Android platform, demonstrating the basic facilities that applications will use.
 
The files contained here:


AndroidManifest.xml

This XML file describes to the Android platform what your application can do.
It is a required file, and is the mechanism you use to show your application to the user (in the app launcher's list), handle data types, etc.


src/*

Under this directory is the Java source for the application.


src/com.moodle/DocsData.java
src/com.moodle/DocsFragment.java
src/com.moodle/EncryptPassword.java
src/com.moodle/Login.java
src/com.moodle/MyDBHandler.java
src/com.moodle/NoticesData.java
src/com.moodle/NoticesFragment.java
src/com.moodle/Question.java
src/com.moodle/QuizData.java
src/com.moodle/QuizSolution.java
src/com.moodle/SavedSolution.java
src/com.moodle/Solution.java
src/com.moodle/Staff.java
src/com.moodle/Staff_Home.java
src/com.moodle/StaffQuizFragment.java
src/com.moodle/StaffQuizResponses.java
src/com.moodle/CustomList.java
src/com.moodle/StaffViewResponse.java
src/com.moodle/CustomListViewResponse.java
src/com.moodle/ListViewHolder2.java
src/com.moodle/Student.java
src/com.moodle/Student_Home.java
src/com.moodle/StudentAnswerQuestion.java
src/com.moodle/StudentQuizFragment.java
src/com.moodle/StudentQuizQuestions.java
src/com.moodle/StudentQuizQuestionsFragment.java
src/com.moodle/TableData.java
src/com.moodle/UploadQuiz.java
src/com.moodle/UploadQuizListAdapter.java
src/com.moodle/ListViewHolder.java


This is the implementation of the "activity" feature described in AndroidManifest.xml.  The path each class implementation is
{src/PACKAGE/CLASS.java}, where PACKAGE comes from the name in the <package> tag and CLASS comes from the class in the <activity> tag.


res/*

Under this directory are the resources for the application.


res/layout/activity_staff_home.xml
res/layout/activity_staff_quiz_responses.xml
res/layout/activity_staff_view_response.xml
res/layout/activity_student_answer_question.xml
res/layout/activity_student_home.xml
res/layout/activity_student_quiz_questions.xml
res/layout/activity_upload_quiz.xml
res/layout/app_bar_staff_home.xml
res/layout/app_bar_student_home.xml
res/layout/custom_list_item.xml
res/layout/custom_quiz_folder.xml
res/layout/custom_response_list_item.xml
res/layout/custom_student_question.xml
res/layout/custom_view_response_item.xml
res/layout/fragment_docs.xml
res/layout/fragment_notices.xml
res/layout/fragment_quiz_folder.xml
res/layout/fragment_student_quiz_questions.xml
res/layout/login.xml
res/layout/nav_header_staff_home.xml
res/layout/nav_header_student_home.xml
res/layout/quiz_ques.xml
res/layout/upload_docs_notices.xml

The res/layout/ directory contains XML files describing user interface view hierarchies.  The logm.xml file here is used by
Login.java to construct its UI.  The base name of each file (all text before a '.' character) is taken as the resource name;
it must be lower-case. Similar other xml pages are also used by their corresponding java classes to provide UIs.


res/drawable/add.png
res/drawable/delete.png
res/drawable/download.png
res/drawable/icon.png
res/drawable/login_btn.png
res/drawable/main_background.png
res/drawable/moodle_icon.png
res/drawable/notices.png
res/drawable/quiz.png
res/drawable/quiz_folder.png
res/drawable/quiz_ques.png
res/drawable/textbox.png
res/drawable/side_nav_bar.xml
res/drawable/textbox.png
res/drawable/welcome.png


The res/drawable/ directory contains images and other things that can be drawn to the screen.  These can be bitmaps (in .png or .jpeg format) or
special XML files describing more complex drawings. Like layout files, the base name is used for the resulting resource name.

res/menu/activity_staff_home_drawer.xml
res/menu/activity_student_home_drawer.xml
res/menu/delete_files.xml
res/menu/menu_staff_tab.xml
res/menu/staff_home.xml
res/menu/student_home.xml
res/values/colors.xml
res/values/strings.xml
res/values/styles.xml

These XML files describe additional resources included in the application.
They all use the same syntax; all of these resources could be defined in one
file, but we generally split them apart as shown here to keep things organized.