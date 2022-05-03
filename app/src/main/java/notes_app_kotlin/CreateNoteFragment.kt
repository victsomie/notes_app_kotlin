package notes_app_kotlin

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.coroutines.launch
import notes_app_kotlin.database.NotesDatabase
import notes_app_kotlin.entities.Notes
import notes_app_kotlin.util.NoteBottomSheetFragment
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteFragment : BaseFragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {


    var selectedColor = "#171C26"
    var currentDate: String? = null

    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456


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

        // Register broadcast receiver : REMEMBER TO UNREGISTER INSIDE ONDESTROY
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(BroadcastReceiver, IntentFilter("bottom_sheet_action"))

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())
        colorView.setBackgroundColor(Color.parseColor(selectedColor))

        tvDateTime.text = currentDate

        imgDone.setOnClickListener {
            saveNote()
            // replaceFragment(HomeFragment.newInstance(), true)
        }

        imgBack.setOnClickListener {

//            replaceFragment(HomeFragment.newInstance(), false)

            requireActivity().supportFragmentManager.popBackStack()
        }


        imgMore.setOnClickListener {
            var noteBottomSheetFragment = NoteBottomSheetFragment.newInstance()
            noteBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "Note Bottom Sheet Fragment"
            )
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


    // Broadcast receiver to receive actions
    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val actionColor = p1!!.getStringExtra("action")

            when (actionColor!!) {
                "Blue" -> {

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Yellow" -> {

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Purple" -> {

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Green" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }
                "Orange" -> {

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Black" -> {

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Image" -> {

//                    Toast.makeText(requireActivity(), "SEARCHING PERMISSIONS", Toast.LENGTH_SHORT).show()

                    readStorageTask()

                }

                else -> {

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }
    }


    override fun onDestroy() {

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
        super.onDestroy()

    }

    private fun hasReadStoragePerm(): Boolean {

        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun hasWriteStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }

    private fun readStorageTask() {
        if (hasReadStoragePerm()) {

            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
            pickImageFromGallery()
        } else {
            Toast.makeText(
                requireContext(),
                "Ask for Permissions to be granted",
                Toast.LENGTH_SHORT
            ).show()

            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.read_storage_permission_text),
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                if (data != null) {
                    var selectedImgUrl = data.data
                    if (selectedImgUrl != null) {

                        try {

                            var inputStream =
                                requireActivity().contentResolver.openInputStream(selectedImgUrl)
                            var bitmap = BitmapFactory.decodeStream(inputStream)
                            layoutImage.visibility = View.VISIBLE
                            imgNote.visibility = View.VISIBLE
                            imgNote.setImageBitmap(bitmap)

                            selectedImagePath = getPathFromUri(selectedImgUrl)!!

                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }

    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        resultLauncher.launch(intent)

    }


    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        Toast.makeText(requireContext(), "RETURNING FROM IMAGE", Toast.LENGTH_SHORT).show()

        if (requestCode == REQUEST_CODE_IMAGE && requestCode == RESULT_OK) {


            if (data != null) {
                var selectedImgUrl = data.data
                if (selectedImgUrl != null) {

                    try {

                        var inputStream =
                            requireActivity().contentResolver.openInputStream(selectedImgUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        layoutImage.visibility = View.VISIBLE
                        imgNote.visibility = View.VISIBLE
                        imgNote.setImageBitmap(bitmap)


                        selectedImagePath = getPathFromUri(selectedImgUrl)!!

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

     */


    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireActivity()
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
}
