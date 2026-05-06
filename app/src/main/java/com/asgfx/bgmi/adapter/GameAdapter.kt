package com.asgfx.bgmi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asgfx.bgmi.R
import com.asgfx.bgmi.models.GameModel

class GameAdapter(
    private val games: List<GameModel>,
    private val onGameClick: (String) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivGameIcon)
        val name: TextView = view.findViewById(R.id.tvGameName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.name.text = game.name
        holder.icon.setImageDrawable(game.icon)
        holder.itemView.setOnClickListener { onGameClick(game.packageName) }
    }

    override fun getItemCount() = games.size
}
