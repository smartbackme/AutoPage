package com.qdedu.autopage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.qdedu.autopage.ui.main.ApFragmentSimpleFragment
import com.qdedu.autopage.ui.main.FragmentSimpleFragment
@AutoPage
class FragmentSimpleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_simple_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ApFragmentSimpleFragment.getInstance().setMessage("134").build())
                .commitNow()
        }

    }
}