from flask import Flask
import jsonify
import requests

app = Flask(__name__)

@app.route('/')
def home():
   return 'This is Home!'

@app.route('/test', methods = ['GET'])
def test():
    return "Success"

if __name__ == '__main__':  
   app.run('0.0.0.0', port=5000, debug=True)