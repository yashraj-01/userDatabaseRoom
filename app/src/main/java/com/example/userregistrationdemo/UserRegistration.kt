package com.example.userregistrationdemo

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import androidx.room.Room
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class UserRegistration : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var container: RelativeLayout
    lateinit var fragmentLayout: RelativeLayout
    lateinit var usersListView: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var newUserButton: MaterialButton
    lateinit var addUserFragment: AddNewUserFragment
    lateinit var userFragment: UserFragment
    lateinit var users: MutableList<String>
    lateinit var db: UsersDatabase
    lateinit var selectedUser: User
    private var position: Int = 0

    lateinit var sharedPreferences: SharedPreferences

    suspend fun refreshList(){
        withContext(Dispatchers.Default){
            users.clear()
            users.addAll(db.userDao().sortedFind() as MutableList<String>)
        }
        withContext(Dispatchers.Main){
            adapter.notifyDataSetChanged()
        }
    }

    private fun findViews(){
        container = findViewById(R.id.container)
        fragmentLayout = findViewById(R.id.fragmentLayout)
        usersListView = findViewById(R.id.usersListView)
        newUserButton = findViewById(R.id.newUserButton)
    }

    fun addNewUser(view: View){
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentLayout.visibility = View.VISIBLE

        fragmentTransaction.replace(R.id.fragmentLayout,addUserFragment).commit()

        container.visibility = View.GONE
    }

    private suspend fun fetchUser(){
        db = UsersDatabase.getInstance(this)
        withContext(Dispatchers.Default){
            users = db.userDao().sortedFind() as MutableList<String>
        }
        withContext(Dispatchers.Main){
            adapter = ArrayAdapter(this@UserRegistration,android.R.layout.simple_list_item_1,users)

            usersListView.adapter = adapter
        }

    }

    private suspend fun loadUser(){
        withContext(Dispatchers.Default){
            selectedUser = db.userDao().loadByName(users[position])
        }
        withContext(Dispatchers.Main){
            fragmentLayout.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.fragmentLayout,userFragment).commit()
            container.visibility = View.GONE
        }
    }

    private suspend fun deleteUser(){
        withContext(Dispatchers.Default){
            db.userDao().delete(db.userDao().loadByName(users[position]))
            users.removeAt(position)
        }
        withContext(Dispatchers.Main){
            adapter.notifyDataSetChanged()
            Toast.makeText(this@UserRegistration,"User deleted successfully",Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun deleteAllUsers(){
        withContext((Dispatchers.Default)){
            db.userDao().nukeTable()
            users.clear()
            users.addAll(db.userDao().sortedFind() as MutableList<String>)
        }

        withContext(Dispatchers.Main){
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        return when(item.itemId){
            R.id.clear -> {
                launch {
                    deleteAllUsers()
                }
                true
            }

            R.id.logout -> {
                sharedPreferences.edit().putBoolean("authorized",false).apply()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
                true
            }

            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration)

        findViews()

        sharedPreferences = getSharedPreferences("admin", Context.MODE_PRIVATE)

        supportActionBar?.title = "Users"

        addUserFragment = AddNewUserFragment.newInstance(null, null)
        userFragment = UserFragment.newInstance(null, null)

        launch {
            fetchUser()
        }

        usersListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            this.position = position
            launch {
                loadUser()
            }
        }

        usersListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                this.position = position
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setTitle("Delete User")
                    .setMessage("Do you want to delete this user?")
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                        launch {
                            deleteUser()
                        }
                    })
                    .setNegativeButton("No",null)
                    .show()
                return@OnItemLongClickListener true
            }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (addUserFragment.isVisible) {
            supportActionBar?.title = "Users"
            supportFragmentManager.beginTransaction().remove(addUserFragment).commit()
            adapter.notifyDataSetChanged()
            container.visibility = View.VISIBLE
            fragmentLayout.visibility = View.INVISIBLE
        } else if (userFragment.isVisible){
            supportActionBar?.title = "Users"
            supportFragmentManager.beginTransaction().remove(userFragment).commit()
            container.visibility = View.VISIBLE
            fragmentLayout.visibility = View.INVISIBLE
        } else {
            super.onBackPressed();
        }
    }
}