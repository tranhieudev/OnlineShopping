package com.example.onlineshopping.ui.fragment.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.onlineshopping.R
import com.example.onlineshopping.data.model.User
import com.example.onlineshopping.databinding.FragmentRegisterBinding
import com.example.onlineshopping.utils.extention.isEmailValid
import com.example.onlineshopping.utils.extention.isPasswordValid
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment(), RegisterListenner {

    lateinit var binding: FragmentRegisterBinding
    val auth = Firebase.auth
    var database = Firebase.database.reference
    val viewmodel: RegisterViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_register, container, false)

        binding.viewModel = viewmodel

        binding.setVariable(BR.registerListenner, this)

        binding.lifecycleOwner = viewLifecycleOwner
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun btnRegister(view: View)  {
        Toast.makeText(context, "adsc", Toast.LENGTH_LONG).show()
        val email = binding.editEmail.text.toString()
        val name = binding.editUserName.text.toString()
        val phone = binding.editPhoneNumber.text.toString()
        val password = binding.editPassword.text.toString()
        val confirmpassword = binding.editConfirm.text.toString()

        val check = checkValidate(
            email, name, phone, password, confirmpassword
        )

        if (check) {
            /*
                save model
                register
                push up user
             */

            viewmodel.user = User("", name, email, phone, password)
            register(email, password)
        }
    }


    private fun writeNewUser(user: User) {

        Firebase.database.reference.child("users").child(user.uuid!!).setValue(user).addOnSuccessListener {
            Log.d("Register", "success")
        }
            .addOnFailureListener{
                Log.d("Register", it.toString())
            }

        Log.d("Register", "uuid : ${user.uuid}")

    }

    private fun register(email: String, password: String): Boolean {

        //if register success is save model and push up database and go to main
        var check = false
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

            viewmodel.user = User(it.user?.uid, viewmodel.user.name, viewmodel.user.email, viewmodel.user.phone, viewmodel.user.password)
            writeNewUser(viewmodel.user)
            findNavController().navigate(R.id.mainFragment)
            Log.d("Register", "Register success ${it.user?.uid}")
            check = true
        }
            .addOnFailureListener {
                Log.d("Register", "Register fail  ${it.toString()}")
                check = false
            }

        return check
    }

    private fun checkValidate(
        email: String,
        name: String,
        phone: String,
        password: String,
        confirmpassword: String
    ): Boolean {
        var check = true
        if (!email.isEmailValid()) {
            check = false
            binding.loEmail.error = resources.getString(R.string.err_email)
        } else binding.loEmail.error = null

        if (name.isEmpty()) {
            check = false
            binding.loName.error = resources.getString(R.string.err_null)
        } else binding.loName.error = null

        if (phone.length < 9 || phone.length > 13) {
            check = false
            binding.loPhoneNumber.error = resources.getString(R.string.err_not_phone)
        } else binding.loName.error = null

        if (!isPasswordValid(password)) {
            check = false
            binding.loPassword.error = resources.getString(R.string.err_not_password)
        } else binding.loName.error = null

        if (password != confirmpassword) {
            Log.d("Register", "password  $password=$confirmpassword=")
            check = false
            binding.loConfirmPassword.error = resources.getString(R.string.err_confirmpass)
        } else binding.loConfirmPassword.error = null

        Log.d("Register", "check  $check")

        return check
    }

}
