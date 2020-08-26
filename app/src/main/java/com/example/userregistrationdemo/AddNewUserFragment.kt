package com.example.userregistrationdemo

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddNewUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNewUserFragment : Fragment(), CoroutineScope by MainScope() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var nameEditText: TextInputEditText
    private lateinit var genderTextInputLayout: TextInputLayout
    private lateinit var ageEditText: TextInputEditText
    private lateinit var phoneNumberEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var addUserButton: MaterialButton

    private suspend fun addUser(name:String, gender:String, age:String, phoneNumber:String, address:String){
        val db = UsersDatabase.getInstance(requireContext())
        val user = User(name, gender, age, phoneNumber, address)
        withContext(Dispatchers.Default){
            db.userDao().insertUser(user)
        }
        withContext(Dispatchers.Main){
            Toast.makeText(requireContext(),"User Added Successfully",Toast.LENGTH_SHORT).show()
            (activity as UserRegistration).refreshList()
            activity?.supportFragmentManager?.beginTransaction()?.remove(this@AddNewUserFragment)?.commit()
            (activity as UserRegistration).container.visibility = View.VISIBLE
            (activity as UserRegistration).fragmentLayout.visibility = View.INVISIBLE
        }

    }

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_new_user, container, false)

        (activity as UserRegistration).supportActionBar?.title = "Add User"

        nameEditText = view.findViewById(R.id.nameEditText)
        genderTextInputLayout = view.findViewById(R.id.genderTextInputlayout)
        ageEditText = view.findViewById(R.id.ageEditText)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        addressEditText = view.findViewById(R.id.addressEditText)
        addUserButton = view.findViewById(R.id.addUserButton)

        val items = listOf("Male", "Female", "Others")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (genderTextInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        addUserButton.setOnClickListener(View.OnClickListener {
            if (nameEditText.text.toString().isNotEmpty() && genderTextInputLayout.editText?.text.toString().isNotEmpty() && ageEditText.text.toString().isNotEmpty() && phoneNumberEditText.text.toString().isNotEmpty() && addressEditText.text.toString().isNotEmpty()){
                val name = nameEditText.text.toString()
                val gender = genderTextInputLayout.editText?.text.toString()
                val age = ageEditText.text.toString()
                val phoneNumber = phoneNumberEditText.text.toString()
                val address = addressEditText.text.toString()
                launch {
                    addUser(name, gender, age, phoneNumber, address)
                }
            }else{
                Toast.makeText(requireContext(),"Please fill all fields",Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddNewUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            AddNewUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}