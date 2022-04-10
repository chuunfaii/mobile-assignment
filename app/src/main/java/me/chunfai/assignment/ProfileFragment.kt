package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels

class ProfileFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val textView = view.findViewById<TextView>(R.id.textView)

        sharedViewModel.user.observe(viewLifecycleOwner) {
            textView.text = it.firstName + " " + it.lastName
        }

//        sharedViewModel.restaurants.observe(viewLifecycleOwner) {
//            Toast.makeText(view.context, it.toString(), Toast.LENGTH_LONG).show()
//        }

        return view
    }

}