@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package org.example.project.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import org.example.project.data.LocalAppStrings
import org.example.project.theme.AppColors
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSSortDescriptor
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.writeToURL
import platform.Photos.PHAsset
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHChange
import platform.Photos.PHFetchOptions
import platform.Photos.PHImageContentModeAspectFill
import platform.Photos.PHImageContentModeAspectFit
import platform.Photos.PHImageManager
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHImageRequestOptionsDeliveryModeOpportunistic
import platform.Photos.PHPhotoLibrary
import platform.Photos.PHPhotoLibraryChangeObserverProtocol
import platform.PhotosUI.presentLimitedLibraryPickerFromViewController
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
internal fun LimitedPhotoGrid(
    onPick: (String) -> Unit,
    onCancel: () -> Unit,
    hostController: () -> UIViewController?,
) {
    var assetsVersion by remember { mutableStateOf(0) }
    val assets = remember(assetsVersion) { fetchAuthorizedAssets() }
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    var loading by remember { mutableStateOf(false) }
    var picked by remember { mutableStateOf(false) }
    val str = LocalAppStrings.current

    DisposableEffect(Unit) {
        val observer = PhotoLibraryObserver { runOnMain { assetsVersion += 1 } }
        PHPhotoLibrary.sharedPhotoLibrary().registerChangeObserver(observer)
        onDispose {
            PHPhotoLibrary.sharedPhotoLibrary().unregisterChangeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = insets.calculateTopPadding())
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = str.cancel,
                    color = AppColors.Accent,
                    modifier = Modifier
                        .clickable(onClick = onCancel)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = str.gridChoosePhoto,
                    color = AppColors.OnSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = str.gridManage,
                    color = AppColors.Accent,
                    modifier = Modifier
                        .clickable {
                            val host = hostController() ?: return@clickable
                            PHPhotoLibrary.sharedPhotoLibrary()
                                .presentLimitedLibraryPickerFromViewController(host) { _ ->
                                    runOnMain { assetsVersion += 1 }
                                }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }

            if (assets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = str.gridNoAccessible,
                        color = AppColors.OnSurfaceMuted,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = insets.calculateBottomPadding()),
                ) {
                    items(assets, key = { it.localIdentifier }) { asset ->
                        AssetThumb(
                            asset = asset,
                            onClick = {
                                if (loading || picked) return@AssetThumb
                                loading = true
                                requestFullSizePath(asset) { path ->
                                    loading = false
                                    if (path != null && !picked) {
                                        picked = true
                                        onPick(path)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = AppColors.Accent,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}

@Composable
private fun AssetThumb(asset: PHAsset, onClick: () -> Unit) {
    var image by remember(asset.localIdentifier) { mutableStateOf<UIImage?>(null) }
    LaunchedEffect(asset.localIdentifier) {
        requestThumbnail(asset, sizePx = 360) { image = it }
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(AppColors.SurfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        val current = image
        if (current != null) {
            UIKitView(
                factory = {
                    UIImageView().apply {
                        contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                        clipsToBounds = true
                        userInteractionEnabled = false
                        this.image = current
                    }
                },
                update = { it.image = current },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            CircularProgressIndicator(
                color = AppColors.Accent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
        )
    }
}

private fun fetchAuthorizedAssets(): List<PHAsset> {
    val opts = PHFetchOptions().apply {
        sortDescriptors = listOf(
            NSSortDescriptor.sortDescriptorWithKey("creationDate", ascending = false)
        )
    }
    val fetched = PHAsset.fetchAssetsWithMediaType(
        PHAssetMediaTypeImage,
        options = opts,
    )
    val out = mutableListOf<PHAsset>()
    val count = fetched.count.toInt()
    for (i in 0 until count) {
        val obj = fetched.objectAtIndex(i.toULong())
        (obj as? PHAsset)?.let { out.add(it) }
    }
    return out
}

private fun requestThumbnail(asset: PHAsset, sizePx: Int, onResult: (UIImage?) -> Unit) {
    val opts = PHImageRequestOptions().apply {
        deliveryMode = PHImageRequestOptionsDeliveryModeOpportunistic
        networkAccessAllowed = true
    }
    val size = CGSizeMake(sizePx.toDouble(), sizePx.toDouble())
    PHImageManager.defaultManager().requestImageForAsset(
        asset,
        size,
        PHImageContentModeAspectFill,
        opts,
    ) { img, _ ->
        runOnMain { onResult(img) }
    }
}

private fun requestFullSizePath(asset: PHAsset, onResult: (String?) -> Unit) {
    val opts = PHImageRequestOptions().apply {
        deliveryMode = PHImageRequestOptionsDeliveryModeOpportunistic
        networkAccessAllowed = true
        synchronous = false
    }
    val targetSize = CGSizeMake(1500.0, 1500.0)
    var delivered = false
    PHImageManager.defaultManager().requestImageForAsset(
        asset,
        targetSize,
        PHImageContentModeAspectFit,
        opts,
    ) { img, info ->
        if (delivered) return@requestImageForAsset
        if (img != null) {
            delivered = true
            runOnMain {
                val path = encodeImageAndSave(img)
                onResult(path)
            }
            return@requestImageForAsset
        }
        val cancelled = (info?.get("PHImageCancelledKey") as? Boolean) == true
        val failed = info?.get("PHImageErrorKey") != null
        if (cancelled || failed) {
            delivered = true
            runOnMain { onResult(null) }
        }
    }
}

private fun encodeImageAndSave(image: UIImage): String? {
    val jpeg: NSData = UIImageJPEGRepresentation(image, 0.85) ?: return null
    val tmpDir = NSTemporaryDirectory()
    val name = "pet_${NSUUID().UUIDString}.jpg"
    val path = "$tmpDir$name"
    val url = NSURL.fileURLWithPath(path)
    val ok = jpeg.writeToURL(url, atomically = true)
    return if (ok) path else null
}

private class PhotoLibraryObserver(
    private val onChange: () -> Unit,
) : NSObject(), PHPhotoLibraryChangeObserverProtocol {
    override fun photoLibraryDidChange(changeInstance: PHChange) {
        onChange()
    }
}
