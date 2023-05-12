package my.edu.tarc.epf.ui.profile

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import my.edu.tarc.epf.R
import my.edu.tarc.epf.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.prefs.AbstractPreferences


class ProfileFragment : Fragment(), MenuProvider {
    private var _binding: FragmentProfileBinding? = null //This ? variable can hold a null value
    private val binding get() = _binding!!

    //Implicit intent = tell the system i want to do somethings, i did not specify who do
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.imageViewProfile.setImageURI(uri)
        }
    }

    private lateinit var sharedPre: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(
            inflater,
            container, false
        )
        return binding.root
    }

    //Let this fragment take part of control of top menu
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Add Menu Host
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            this, viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        val image = readProfilePicture()
        if (image != null) {
            binding.imageViewProfile.setImageBitmap(image)
        } else {
            binding.imageViewProfile.setImageResource(R.drawable.default_pic)
        }

        //Do the click event for image
        binding.imageViewProfile.setOnClickListener {
            //Invoke Intent here
            getPhoto.launch("image/*") //i want image, and any format
        }

        //Setup Shared Preference
        sharedPre = requireActivity().getPreferences(Context.MODE_PRIVATE)
        //getsharedPreferecnes = multiple activity can access it
        //getPreference = only one activity can access it
        //Fragment is the subset of activity, so multiple fragment can access this files also

        //read value from share preference
        val name = sharedPre.getString(getString(R.string.name), getString(R.string.nav_header_title))
        val email = sharedPre.getString(getString(R.string.email),
        getString(R.string.nav_header_subtitle))

        binding.editTextName.setText(name)
        binding.editTextEmailAddress.setText(email)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.profile_menu, menu)
        //Remove the about and settings from the menu
        menu.findItem(R.id.action_about).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_save) {
            //TODO : Save user profile info and picture
            saveProfilePicture(binding.imageViewProfile)

            //Save profile info
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmailAddress.text.toString()
            with(sharedPre.edit()){
                putString(getString(R.string.name),name)
                putString(getString(R.string.email),email)
                apply()
            }
            Toast.makeText(context,getString(R.string.profile_save),Toast.LENGTH_SHORT).show()


            //Update nav header
            val navHeaderView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
            val headerView = navHeaderView.getHeaderView(0)

            val textViewName : TextView = headerView.findViewById(R.id.textViewName)
            val textViewEmail : TextView = headerView.findViewById(R.id.textViewEmail)
            val imageViewPicture : ImageView = headerView.findViewById(R.id.imageViewPicture)

            imageViewPicture.setImageBitmap(readProfilePicture())
            textViewName.text = name
            textViewEmail.text = email

        } else if (menuItem.itemId == android.R.id.home) {
            //Handling the up button
            findNavController().navigateUp()
        }
        return true
    }



    private fun saveProfilePicture(view: View) {
        val filename = "profile.png" //fix the profile name
        val file = File(this.context?.filesDir, filename) //this is to open the files explorer
        //Every app run , it will generate a package with your name in the data folder
        val image = view as ImageView

        val bd = image.drawable as BitmapDrawable //cast image to bitmap
        val bitmap = bd.bitmap
        val outputStream: OutputStream

        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream) //compress 50% value
            outputStream.flush() //u must flush it, if not iw will remain
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun readProfilePicture(): Bitmap? {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)

        if (file.isFile) {//if files exist
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                return bitmap
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        return null
    }


}