from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/')
def home():
   return 'This is Home!'

@app.route('/test', methods = ['GET'])
def test():
    return "Success"
 
@app.route('/api/bloggpt', methods = ['POST'])
def getBlog() :
       blog = request.get_json()
       return jsonify(blog)

if __name__ == '__main__':  
   app.run('0.0.0.0', port=5000, debug=True)