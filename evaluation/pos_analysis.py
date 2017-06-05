import pandas as pd
import os.path as path


def analyze_data(data_path):
    data = prepare_data_frame(data_path)

    base = "results/" + path.splitext(path.basename(data_path))[0]
    results_per_split = analyze_splits(data)
    write_to_file(base + '--results_per_split.txt',
                  results_per_split.to_string())

    summary = compute_summary(results_per_split)
    write_to_file(base + '--summary.txt', summary.to_string())


def analyze_splits(data):
    results = pd.DataFrame()
    results['split'] = pd.Series.unique(data.splitNumber)
    grouped_data = data.groupby('splitNumber')
    results['accuracy-total'] = grouped_data.apply(accuracy)
    known_split_acc = data.groupby(
        ['isKnownWord', 'splitNumber']).apply(accuracy)
    results['accuracy-known'] = known_split_acc[True]
    results['accuracy-unknown'] = known_split_acc[False]
    results['tokens'] = grouped_data['word'].count()
    results['fraction-unknown'] = 1 - \
        grouped_data['isKnownWord'].sum() / results.tokens
    results['sentences'] = grouped_data.apply(count_sentences)
    return results


def accuracy(data):
    return data['isCorrect'].sum() / len(data)


def count_sentences(data):
    return len(data['sentenceIndex'].unique())


def compute_summary(results):
    selection = results[['accuracy-total', 'accuracy-known', 'accuracy-unknown',
                         'fraction-unknown']]
    d = {'mean': selection.mean(), 'std': selection.std(ddof=0)}
    return pd.DataFrame(d)


def prepare_data_frame(data_path):
    data = pd.read_csv(data_path)
    data['isCorrect'] = data['expectedTag'] == data['actualTag']
    return data


def write_to_file(filename, content):
    f = open(filename, 'w')
    f.write(content)
    f.close()


if __name__ == '__main__':
    # n = 3
    analyze_data('../data/private/10-Fold_Cross_Validation_Tiger_n3.csv')

    # n = 4
    analyze_data('../data/private/10-Fold_Cross_Validation_Tiger_n4.csv')
