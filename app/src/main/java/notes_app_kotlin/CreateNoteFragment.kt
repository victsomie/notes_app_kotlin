package notes_app_kotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.coroutines.launch
import notes_app_kotlin.database.NotesDatabase
import notes_app_kotlin.entities.Notes
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteFragment : BaseFragment() {


    var selectedColor = "#171C26"
    var currentDate: String? = null
    private var webLink = ""

    private var selectedImagePath = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


    fun saveNote() {

        if (etNoteTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Title required", Toast.LENGTH_SHORT).show();

            return
        }
        if (etNoteSubTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Sub title required", Toast.LENGTH_SHORT).show();
            return
        }

        if (etNoteDesc.text.isNullOrEmpty()) {
            Toast.makeText(context, "Note Description is required", Toast.LENGTH_SHORT).show();

            return
        } else {


            launch {
                val notes = Notes()

                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink


                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    etNoteTitle.setText("")
                    etNoteSubTitle.setText("")
                    etNoteDesc.setText("")
                    layoutImage.visibility = View.GONE
                    imgNote.visibility = View.GONE
                    tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())

        tvDateTime.text = currentDate

        imgDone.setOnClickListener {
            saveNote()
           // replaceFragment(HomeFragment.newInstance(), true)
        }

    }

    fun replaceFragment(fragment: Fragment, istransition: Boolean) {

        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        Log.d("tegegegegeee", "----------------------------------------")

        if (istransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }

        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(fragment.javaClass.simpleName)

        fragmentTransition.commit()

    }
}