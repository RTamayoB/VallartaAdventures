package com.exinnotech.vallartaadventures.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.exinnotech.vallartaadventures.databinding.ActivityLoginBinding
import com.exinnotech.vallartaadventures.R
import com.exinnotech.vallartaadventures.SearchActivity
import com.exinnotech.vallartaadventures.room.VallartaApplication
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.viewmodel.ReservationViewModel
import com.exinnotech.vallartaadventures.room.viewmodel.ReservationViewModelFactory
import com.exinnotech.vallartaadventures.scanning.ScanActivity

class LoginActivity : AppCompatActivity() {

    //TODO: Remove test print data
    private val reservationViewModel: ReservationViewModel by viewModels {
        ReservationViewModelFactory((application as VallartaApplication).reservationRepository)
    }

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val tryPrint = binding.tryPrintButton

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this))
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        /*
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })*/

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                /*
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
                 */
                loading.visibility = View.VISIBLE
                val queue = Volley.newRequestQueue(this@LoginActivity)

                val jsonArrayRequestTours = StringRequest(
                    Request.Method.GET, "http://exinnot.ddns.net:10900/AppUsuarios/CheckUser?UserName=${username.text.toString()}&Password=${password.text.toString()}",
                    { response ->
                        loading.visibility = View.INVISIBLE
                        Log.d("Result", response.toString())
                        val shared = context.getSharedPreferences("login",0)
                        shared.edit().putString("uid", response).apply()
                        shared.edit().putString("username", username.text.toString()).apply()
                        Toast.makeText(
                            applicationContext,
                            "Bienvenido ${username.text.toString()}",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@LoginActivity, SearchActivity::class.java)
                        startActivity(intent)
                    },
                    { error ->
                        loading.visibility = View.INVISIBLE
                        Log.d("Error", error.toString())
                        Toast.makeText(
                            applicationContext,
                            "Error en Login",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
                queue.add(jsonArrayRequestTours)
            }
        }

        tryPrint.setOnClickListener {
            val reservation = Reservation(
                1,
                1,
                "Israel",
                "A123BC",
                "Agencia 1",
                "Miravalle",
                "1",
                "Tour Altavista",
                "Español",
                "2021-07-15T12:00:00",
                "2021-07-15T12:00:00",
                "2021-07-18T13:00:00",
                "micorreo@gmail.com",
                "3313560433",
                1000,
                "A123BC",
                "10:00:00",
                1000,
                1,
                1,
                1,
                1,
                17,
            )
            val printer = ScanActivity(this, reservation)
            printer.connectPrinter()
            printer.printPasses(reservation)
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}