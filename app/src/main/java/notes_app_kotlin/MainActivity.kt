package notes_app_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("tegegegegeee MAIN", "----------------------------------------")


        replaceFragment(HomeFragment.newInstance(), false)

    }

    fun replaceFragment(fragment: Fragment, istransition: Boolean) {

        val fragmentTransition = supportFragmentManager.beginTransaction()

        Log.d("tegegegegeee", "----------------------------------------")

        if (istransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }

        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName)

        fragmentTransition.commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        val fragments = supportFragmentManager.fragments
        if (fragments.size == 0){
            finish()
        }
    }
}