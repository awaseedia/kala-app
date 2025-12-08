package hu.bme.ait.kalaapp.ui.screens.product

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import hu.bme.ait.kalaapp.R
import hu.bme.ait.kalaapp.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Load product on first composition
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    // Save/Favorite Button
                    if (uiState.product != null) {
                        IconButton(
                            onClick = {
                                val result = viewModel.toggleFavorite(productId)
                                if (result == "login_required") {
                                    onNavigateToLogin()
                                }
                            },
                            enabled = !uiState.isSavingFavorite
                        ) {
                            Icon(
                                imageVector = if (uiState.isSaved)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Filled.FavoriteBorder,
                                contentDescription = if (uiState.isSaved)
                                    stringResource(R.string.cd_product_saved)
                                else
                                    stringResource(R.string.cd_save_product),
                                tint = if (uiState.isSaved)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.product_detail_error),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProduct(productId) }) {
                            Text(stringResource(R.string.product_detail_retry))
                        }
                    }
                }
            }
            uiState.product != null -> {
                ProductDetailContent(
                    product = uiState.product!!,
                    onVisitWebsite = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.product!!.productUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    onVisitWebsite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Product Image
        AsyncImage(
            model = product.imageUrl,
            contentDescription = stringResource(R.string.cd_product_detail_image),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentScale = ContentScale.Crop
        )

        // Product Info Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Product Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Brand Name - Clickable
            Text(
                text = product.brandName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Navigate to brand - handled by parent */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            Text(
                text = stringResource(R.string.product_price_format, product.price),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category Chip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.product_detail_category),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description Section
            Text(
                text = stringResource(R.string.product_detail_description),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.bio,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Visit Website Button
            Button(
                onClick = onVisitWebsite,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.product_detail_visit_website),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}