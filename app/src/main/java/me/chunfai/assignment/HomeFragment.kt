package me.chunfai.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.tasks.await
import me.chunfai.assignment.databinding.ActivityHomepageBinding
import me.chunfai.assignment.databinding.FragmentHomeBinding
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(R.layout.fragment_home),CoroutineScope {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RestaurantAdapter

    private lateinit var database:FirebaseFirestore

    private lateinit var Restaurant: MutableList<Restaurant>

    private lateinit var sharedViewModel: SharedViewModel

    private var job: Job = Job()

    override val coroutineContext:CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,container:ViewGroup?,
        savedInstanceState:Bundle?
    ): View{
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)

        linearLayoutManager = LinearLayoutManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        database = FirebaseFirestore.getInstance()

        Restaurant = mutableListOf()

        return binding.root
    }

    override fun onResume(){
        super.onResume()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        Restaurant.clear()


//        viewLifecycleOwner.lifecycleScope.launch{
////            getRestaurant(uid)
////            setRecyclerView()
//        }
    }

    private suspend fun getRestaurant(restaurantId:String): Restaurant{
        val restaurantsRef = database.collection("restaurants")
        val snapshot = restaurantsRef.get().await()

        for(document in snapshot.documents){
            if(document.id == restaurantId){
                val id = document.id
                val name = document.get("name").toString()
                val address = document.get("address").toString()
                val openTime = document.get("openTime").toString()
                val closeTime = document.get("closeTime").toString()
                val contact = document.get("contact").toString()
                val description = document.get("description").toString()
                val imageName = document.get("imageName").toString()

                return Restaurant(
                    id,
                    name,
                    address,
                    openTime,
                    closeTime,
                    contact,
                    description,
                    imageName
                )
            }
        }

        return Restaurant()

    }

    private fun setRecyclerView(){
        binding.recyclerView.layoutManager = linearLayoutManager
        adapter = RestaurantAdapter(Restaurant)
        binding.recyclerView.adapter = adapter
    }


//    private val sharedViewModel: SharedViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        val textView = view.findViewById<TextView>(R.id.textView)
//
//        textView.text = sharedViewModel.user?.firstName
//
////        sharedViewModel.user.observe(viewLifecycleOwner) {
////            textView.text = it.firstName + " " + it.lastName
////        }
//
////        sharedViewModel.restaurants.observe(viewLifecycleOwner) {
////            Toast.makeText(view.context, it.toString(), Toast.LENGTH_LONG).show()
////        }
//
//        return view
//    }

}