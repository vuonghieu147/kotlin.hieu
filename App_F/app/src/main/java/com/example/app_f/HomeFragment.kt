package com.example.app_f

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val View = inflater.inflate(R.layout.home, container, false)
        val buttonSpecies = View.findViewById<Button>(R.id.button_species)
        buttonSpecies.setOnClickListener {
            val intent = Intent(requireContext(),Species::class.java)
            startActivity(intent)
        }

        val buttonArticles = View.findViewById<Button>(R.id.button_articles)
        buttonArticles.setOnClickListener {
            val intent = Intent(requireContext(),Articles::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val displayName = currentUser?.displayName?.split(" ")?.firstOrNull()
        val welcomeMessage = View.findViewById<TextView>(R.id.welcome)
        welcomeMessage.text = "Welcome, $displayName"

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val addingNew = View.findViewById<Button>(R.id.button_add)
            addingNew.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Yêu cầu quyền truy cập camera
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION
                    )
                } else {
                    // Quyền truy cập camera đã được cấp
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
        return View
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            capturedPhoto = data?.extras?.get("data") as? Bitmap
            val addNewFragment = AddNewFragment()
            val bundle = Bundle()
            bundle.putParcelable("capturedPhoto", capturedPhoto)
            addNewFragment.arguments = bundle

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, addNewFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */

        private const val REQUEST_CAMERA_PERMISSION = 1001
        private const val REQUEST_IMAGE_CAPTURE = 1002
        private var capturedPhoto: Bitmap? = null
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}