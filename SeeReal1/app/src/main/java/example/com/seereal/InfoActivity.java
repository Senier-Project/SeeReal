package example.com.seereal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false).setImage(R.drawable.about_icon_facebook)
                .setDescription("SeaReal\n어플설명들어갈부분\n개발자: 유수화 배범준 이재현\nGachon Univ.\nSoftware Dept.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with us")
                .addEmail("usuhwa2@gmail.com")
                .addEmail("뱀이메일")
                .addEmail("izg0820@naver.com")
                .addPlayStore("jwh.com.eumme")
                .addGitHub("Mobile-project/Eum-me").create();
        setContentView(aboutPage);
    }
}
