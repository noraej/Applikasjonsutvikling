package com.example.oving3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController

data class Friend(val friendId: Int, var name: String, var phoneNumber: String)

class FriendsView : ViewModel() {

    var friends = mutableStateListOf<Friend>()
        private set

    fun addFriend(name: String, phoneNumber: String) {
        friends.add(Friend(friends.size + 1, name, phoneNumber))
    }

    fun editFriend(id: Int, name: String, phoneNumber: String) {
        friends.filter { it.friendId == id }.forEach {
            it.name = name
            it.phoneNumber = phoneNumber
        }
    }
}

class MainActivity : ComponentActivity() {
    val friendsView by viewModels<FriendsView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main(
                friendsView.friends,
                friendsView::addFriend,
                friendsView::editFriend,
            )
        }
    }
}

@Composable
private fun Main(
    friends: List<Friend>,
    addFriend: (String, String) -> Unit,
    editFriend: (Int, String, String) -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "friendsList") {
        composable(route = "friendsList") {
            FriendsListScreen(navController, friends, addFriend)
        }
        composable(
            route = "editFriend/{friendId}",
            arguments = listOf(navArgument("friendId") {
                type = NavType.IntType
            })
        ) {
            EditFriendScreen(navController, it.arguments!!.getInt("friendId"), friends, editFriend)
        }
    }
}

@Composable
fun FriendsListScreen(
    navController: NavController,
    friends: List<Friend>,
    addFriend: (String, String) -> Unit
) {
    Column {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            item {
                Text(text = "Legg til venn")
                MakeFriend(addFriend)
                Divider()
                Text(text = "Venner")
            }
            items(friends) { friend ->
                FriendInfo(friend, navController)
            }
        }
    }
}

@Composable
fun EditFriendScreen(
    navController: NavController,
    friendId: Int,
    friends: List<Friend>,
    editFriend: (Int, String, String) -> Unit
) {

    val friend = friends.find { it.friendId == friendId } ?: return Scaffold {
        Text("Noe gikk galt")
        Button(onClick = { navController.navigate("friendsList") }) {
            Text("Gå tilbake")
        }
    }
    val nameState = remember { mutableStateOf(TextFieldValue(text = friend.name)) }
    val phoneNumberState = remember { mutableStateOf(TextFieldValue(text = friend.phoneNumber)) }
    Column() {
        Text(
            text = "Rediger informasjon",
            modifier = Modifier.padding(all = 8.dp),
            color = MaterialTheme.colors.primaryVariant,
            style = MaterialTheme.typography.caption,
            )
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Navn") },
            modifier = Modifier.padding(all = 8.dp),
        )
        TextField(
            value = phoneNumberState.value,
            onValueChange = { phoneNumberState.value = it },
            label = { Text("Telefonummer") },
            modifier = Modifier.padding(all = 8.dp),
            )
        Button(
            modifier = Modifier.padding(all = 8.dp),
            onClick = {
                editFriend(friendId, nameState.value.text, phoneNumberState.value.text)
                navController.navigate("friendsList")
            },
        ) {
            Text("Update friend")
        }
    }

}

@Composable
fun FriendInfo(friend: Friend, navController: NavController) {
    //Makes space between each friend view
    Surface(
        shape = MaterialTheme.shapes.medium,
        elevation = 2.dp,
        color = colorResource(R.color.light_purple),
        modifier = Modifier.padding(all = 8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("editFriend/${friend.friendId}") }
        ) {
            //Adding padding around the name and phonenumber
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Text(
                    text = stringResource(R.string.name_tag),
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.subtitle2,

                    )
                //Adding horizontal space between nametag and name
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.body2,
                )
            }
            //Adding padding around the name and phonenumber
            Row(modifier = Modifier.padding(all = 6.dp)) {
                Text(
                    text = stringResource(R.string.phone_number),
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )
                //Adding horizontal space between nametag and name
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = friend.phoneNumber,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}


@Composable
fun MakeFriend(addFriend: (String, String) -> Unit) {
    val nameState = remember { mutableStateOf(TextFieldValue(text = "")) }
    val phoneNumberState = remember { mutableStateOf(TextFieldValue(text = "")) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Navn")},
            modifier = Modifier.padding(all = 6.dp)
            )
        TextField(
            value = phoneNumberState.value,
            onValueChange = { phoneNumberState.value = it },
            label = { Text("Telefonummer") },
            modifier = Modifier.padding(all = 6.dp)
        )
        Button(
            onClick = { addFriend(nameState.value.text, phoneNumberState.value.text)},
            modifier = Modifier.padding(all = 6.dp),
        ) {
            Text("Add friend")
        }
    }
}






