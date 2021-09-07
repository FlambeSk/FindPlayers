window.myapp = {
  counter: 0,
  text: 'AHOJ',
  incrementor: () => {
      window.myapp.counter += 1;
      window.dispatchEvent(new Event('onIncrement'));
  },
  decrementor: () => {
      window.myapp.counter -= 1;
      window.dispatchEvent(new Event('onDecrement'));
  },
  changeText: (text) => {
    window.myapp.text = text;
    window.dispatchEvent(new Event('changeText'));
  }
};