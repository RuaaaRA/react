import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { SparklesIcon, BookOpenIcon, AcademicCapIcon, PlayIcon, StarIcon, LightBulbIcon } from '@heroicons/react/24/outline';
import ThreeDScene from '../components/ThreeDScene.jsx';

const Home = () => {
  return (
    <div className="min-h-screen bg-white text-blue-800 relative overflow-hidden">
      {/* Decorative Background Pattern */}
      <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxwYXRoIGQ9Ik0zNiAzNGM0LjQxOCAwIDgtMy41ODIgOC04cy0zLjU4Mi04LTgtOC04IDMuNTgyLTggOCAzLjU4MiA4IDggOHoiIHN0cm9rZT0iI0Y4NzQwMCIgc3Ryb2tlLW9wYWNpdHk9Ii4wMyIvPjwvZz48L3N2Zz4=')] opacity-10"></div>

      {/* Navbar */}
      <nav className="bg-white/90 backdrop-blur-md shadow-md sticky top-0 z-50 border-b border-blue-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-20 items-center">
            <motion.div 
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ duration: 0.5 }}
              className="flex items-center"
            >
              <SparklesIcon className="h-10 w-10 text-orange-500 animate-pulse" />
              <span className="ml-3 text-2xl font-bold text-blue-800 font-['Poppins']">
                English Fun
              </span>
            </motion.div>
            <div className="flex space-x-4">
              <Link 
                to="/login" 
                className="px-6 py-3 rounded-full bg-gradient-to-r from-blue-600 to-orange-500 text-white font-medium hover:from-blue-700 hover:to-orange-600 transition-all duration-300 shadow-lg hover:shadow-xl hover:-translate-y-0.5 hover:scale-105"
              >
                Sign In
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="flex flex-col-reverse lg:flex-row items-center justify-between gap-12 relative z-10">

          <div className="text-center lg:text-left flex-1">
            <motion.h1 
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8 }}
              className="text-5xl md:text-6xl font-extrabold mb-6 font-['Poppins']"
            >
              <span className="text-blue-700">Learn English</span><br />
              <span className="text-orange-500">the Fun Way!</span>
            </motion.h1>

            <motion.p 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.3, duration: 0.8 }}
              className="text-lg md:text-xl text-blue-600 max-w-xl mx-auto lg:mx-0 mb-8 font-['Comic_Neue'] leading-relaxed"
            >
              Join thousands of kids having fun while learning English with our interactive games, stories, and activities!
            </motion.p>

            <motion.div
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.6, duration: 0.5 }}
              className="flex justify-center lg:justify-start"
            >
              <Link 
                to="/login" 
                className="px-8 py-4 bg-gradient-to-r from-blue-600 to-orange-500 text-white font-bold rounded-full text-lg hover:from-blue-700 hover:to-orange-600 transition-all duration-300 shadow-xl hover:shadow-2xl hover:-translate-y-1 hover:scale-105 flex items-center group"
              >
                Start Learning Now
                <PlayIcon className="h-5 w-5 ml-2 group-hover:translate-x-1 transition-transform" />
              </Link>
            </motion.div>
          </div>

          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.4, duration: 0.8 }}
            className="flex-1 w-full max-w-4xl h-[800px] mx-auto relative z-20 rounded-2xl overflow-hidden shadow-2xl"
          >
            <ThreeDScene />
          </motion.div>
        </div>

        {/* Features Section */}
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1, duration: 0.8 }}
          className="mt-32 grid grid-cols-1 gap-12 sm:grid-cols-2 lg:grid-cols-3"
        >
          {[ 
            {
              icon: <BookOpenIcon className="h-14 w-14 text-orange-500" />,
              title: "Interactive Stories",
              description: "Engaging stories that make learning vocabulary and grammar fun and memorable."
            },
            {
              icon: <AcademicCapIcon className="h-14 w-14 text-orange-500" />,
              title: "Expert Teachers",
              description: "Our friendly teachers make every lesson exciting and easy to understand."
            },
            {
              icon: <SparklesIcon className="h-14 w-14 text-orange-500" />,
              title: "Fun Games",
              description: "Play games that help you practice English without even realizing you're learning!"
            }
          ].map((feature, index) => (
            <motion.div 
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1.2 + index * 0.2 }}
              className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 hover:-translate-y-2 transform border border-blue-100"
            >
              <div className="flex justify-center mb-6">
                <div className="p-4 bg-blue-50 rounded-full shadow-inner">
                  {feature.icon}
                </div>
              </div>
              <h3 className="text-2xl font-bold text-center text-blue-800 mb-4 font-['Poppins']">{feature.title}</h3>
              <p className="text-blue-600 text-center font-['Comic_Neue'] leading-relaxed">{feature.description}</p>
            </motion.div>
          ))}
        </motion.div>

        {/* Extra Decorations Section */}
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.8, duration: 0.8 }}
          className="mt-20 text-center"
        >
          <h2 className="text-3xl font-bold text-orange-500 mb-4">New Adventures Every Week!</h2>
          <p className="text-blue-600 max-w-2xl mx-auto font-['Comic_Neue']">Explore new games, discover new stories, and challenge yourself with exciting activities every time you visit.</p>
          <div className="mt-6 flex justify-center gap-6">
            <StarIcon className="h-10 w-10 text-yellow-400" />
            <LightBulbIcon className="h-10 w-10 text-orange-400" />
            <StarIcon className="h-10 w-10 text-yellow-400" />
          </div>
        </motion.div>
      </main>

      {/* Footer */}
      <footer className="bg-white/90 backdrop-blur-md py-8 mt-16 border-t border-blue-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <p className="text-blue-600 font-['Comic_Neue']">
            © {new Date().getFullYear()} English Fun. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
};

export default Home;
