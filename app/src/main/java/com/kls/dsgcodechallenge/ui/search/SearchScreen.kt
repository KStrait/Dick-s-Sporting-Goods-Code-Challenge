package com.kls.dsgcodechallenge.ui.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.data.NetworkResult

@Composable
fun StoreListScreen(searchViewModel: SearchViewModel = viewModel()) {
    val storeItems by searchViewModel.storeResponse.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stores Near You",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        when (val data = storeItems) {
            is NetworkResult.Error -> Log.d("Stores", data.exception.toString() )
            NetworkResult.Loading -> Log.d("Stores", "Loading" )
            is NetworkResult.Success -> {
                val items = data.data
                StoreList(stores = items)
                Log.d("Stores", "Stores: ${items.size}")
            }
        }
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = "Search")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Search by ZIP Code",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Search",
            tint = Color.Green,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun StoreList(stores: List<StoreResult>) {
    LazyColumn {
        items(stores) { store ->
            StoreItem(store)
        }
    }
}

@Composable
fun StoreItem(store: StoreResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = store.store.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "${store.distance} | ${store.store.location}", fontSize = 14.sp, color = Color.Gray)
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go")
        }
    }
}