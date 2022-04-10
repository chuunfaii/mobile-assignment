package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import me.chunfai.assignment.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var database: FirebaseFirestore

    private lateinit var sharedViewModel: SharedViewModel

    private var restaurants: MutableList<Restaurant> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.floatingActionButton.setOnClickListener {
            Toast.makeText(activity, "Add restaurant", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddRestaurantFragment())
                .addToBackStack("null")
                .commit()
        }

        return binding.root
    }

}