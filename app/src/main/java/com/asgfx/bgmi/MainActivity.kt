// MainActivity.kt mein ye functions update karein
private fun setupStatus() {
    val isInstalled = DeviceUtils.isBGMIInstalled(this)
    binding.tvBgmiStatus.apply {
        if (isInstalled) {
            text = "✓ System Optimized"
            setTextColor(getColor(R.color.colorSuccess))
        } else {
            text = "✗ BGMI Not Found"
            setTextColor(getColor(R.color.colorDanger))
        }
    }
}

private fun initGameLauncher() {
    // Force refresh game list
    val games = DeviceUtils.getInstalledGamesInfo(this)
    if (games.isNotEmpty()) {
        binding.rvGameList.visibility = android.view.View.VISIBLE
        binding.rvGameList.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            adapter = com.asgfx.bgmi.adapter.GameAdapter(games) { pkgName ->
                val launchIntent = packageManager.getLaunchIntentForPackage(pkgName)
                if (launchIntent != null) startActivity(launchIntent)
            }
        }
    } else {
        binding.rvGameList.visibility = android.view.View.GONE
    }
}
