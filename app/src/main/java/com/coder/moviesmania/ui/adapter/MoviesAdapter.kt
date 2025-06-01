package com.coder.moviesmania.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coder.moviesmania.data.model.Movie
import com.coder.moviesmania.databinding.ItemMovieBinding
import com.coder.moviesmania.util.setTextOrGone
import java.text.SimpleDateFormat
import java.util.Locale

class MoviesAdapter : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    private var onItemClickListener: ((Movie) -> Unit)? = null

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(getItem(position))
                }
            }
        }

        fun bind(movie: Movie) {
            binding.movieTitleTextView.setTextOrGone(movie.title)
            binding.movieRatingTextView.setTextOrGone(movie.rating.toString())
            
            // Extract year from release date
            val year = try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = if (movie.releaseDate.isNullOrBlank()) {
                    null
                } else {
                    dateFormat.parse(movie.releaseDate)
                }

                date?.let {
                    SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
                } ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
            
            binding.movieYearTextView.setTextOrGone(year)
            
            // Load poster image using Glide
            val posterUrl = "https://image.tmdb.org/t/p/w342${movie.posterPath}"
            Glide.with(binding.root.context)
                .load(posterUrl)
                .centerCrop()
                .into(binding.moviePosterImageView)
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
} 